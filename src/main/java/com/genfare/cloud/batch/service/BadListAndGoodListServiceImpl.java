package com.genfare.cloud.batch.service;

import static com.genfare.cloud.core.types.ActivityStatusType.CANCELED;
import static com.genfare.cloud.core.types.ActivityStatusType.READY;
import static com.genfare.cloud.core.types.ActivitytypesType.ACCOUNT_GOOD_LIST;
import static com.genfare.cloud.core.types.ActivitytypesType.ACCOUNT_TEMP_BAD_LIST;
import static com.genfare.cloud.core.types.WalletstatusType.ACTIVE;
import static com.genfare.cloud.core.types.WalletstatusType.BAD_LISTED;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genfare.cloud.batch.repository.ActivitiesRepository;
import com.genfare.cloud.batch.repository.PendingactivityRepository;
import com.genfare.cloud.batch.repository.WalletOrganizationRepository;
import com.genfare.cloud.batch.repository.WalletsRepository;
import com.genfare.cloud.batch.resolver.DetectedTenant;
import com.genfare.cloud.core.types.ActivitytypesType;
import com.genfare.cloud.model.entity.Activities;
import com.genfare.cloud.model.entity.ActivityStatuses;
import com.genfare.cloud.model.entity.Activitytypes;
import com.genfare.cloud.model.entity.Pendingactivity;
import com.genfare.cloud.model.entity.Walletorganization;
import com.genfare.cloud.model.entity.Wallets;
import com.genfare.cloud.model.entity.Walletstatus;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class BadListAndGoodListServiceImpl implements BadListAndGoodListService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BadListAndGoodListServiceImpl.class);

	@Autowired
	private WalletsRepository walletsRepository;

	@Autowired
	private PendingactivityRepository pendingActivityRepo;

	@Autowired
	private WalletOrganizationRepository walletOrgRepo;

	@Autowired
	private ActivitiesRepository activityRepo;

	@Autowired
	private DetectedTenant detectedTenant;

	private static final Byte ACTIVE_WALLET = ACTIVE.getIndex();
	private static final Byte BADLIST_WALLET = BAD_LISTED.getIndex();

	@Override
	public void createGoodListAndBadList() throws Exception {
		LOGGER.info("Starting good listing and bad listing Batch Job ");
		detectedTenant.getSupportedTenantNames().forEach(tenantName -> {
			
			detectedTenant.setTenant(tenantName);
			List<BigInteger> cardsToBadlist = walletsRepository.getAccountBasedCardToBadlist();
			LOGGER.info("Tenant name : {} Number of cards to be bad listed {}", tenantName, cardsToBadlist.size());
			updateBadlistedCards(cardsToBadlist);
			List<BigInteger> cardsToGoodlist = walletsRepository.getAccountBasedCardToGoodlist();
			LOGGER.info("Tenant name : {} Number of cards to be good listed {}", tenantName, cardsToGoodlist.size());
			updateGoodlistedCards(cardsToGoodlist);
		});
	}

	private void updateBadlistedCards(List<BigInteger> cardsToBadlist) {
		cardsToBadlist.forEach(badlistCardId -> {
			LOGGER.info("Badlisting card for walletid {} - {}", badlistCardId,detectedTenant.getTenant());
			Optional<Wallets> wallet = walletsRepository.findById(badlistCardId.longValue());
			Pendingactivity pa = createOrUpdatePendingActivity(wallet.get(), ACCOUNT_TEMP_BAD_LIST);
			updateWallet(wallet.get(), BADLIST_WALLET, pa.getAccountLoadSequence());
		});
	}

	private void updateGoodlistedCards(List<BigInteger> cardsToGoodlist) {
		cardsToGoodlist.forEach(goodlistCardId -> {
			LOGGER.info("Goodlisting card for walletid {} - {}", goodlistCardId,detectedTenant.getTenant());
			Optional<Wallets> wallet = walletsRepository.findById(goodlistCardId.longValue());
			Pendingactivity pa = createOrUpdatePendingActivity(wallet.get(), ACCOUNT_GOOD_LIST);
			updateWallet(wallet.get(), ACTIVE_WALLET, pa.getAccountLoadSequence());
		});
	}

	private Pendingactivity createOrUpdatePendingActivity(Wallets wallet, ActivitytypesType type) {
		Pendingactivity pa = cancelExistingPendingActivty(wallet);
		if (!ACCOUNT_GOOD_LIST.getIndex().equals(type.getIndex())) {
			pa = buildPendingactivity(wallet, type);
		}
		return pa;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private Pendingactivity cancelExistingPendingActivty(Wallets wallet) {
		List<Pendingactivity> pendingActivities = pendingActivityRepo
				.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(wallet.getWalletId());
		Pendingactivity existingPendingactivity = null;
		for (Pendingactivity pa : pendingActivities) {
			LOGGER.info("Cancle existing pending activity id {}", pa.getPendingId());
			existingPendingactivity = pa;
			pa.setPendingStatus(new ActivityStatuses(CANCELED.getIndex()));
			Activities activity = pa.getActivity();
			if (null != activity) {
				activity.setStatus(new ActivityStatuses(CANCELED.getIndex()));
			}
			pa.setTouchedBy("lambda-batch");
			pendingActivityRepo.save(pa);
		}
		return existingPendingactivity;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void updateWallet(Wallets wallet, Byte walletstatusId, Short loadSequence) {
		wallet.setWalletstatus(new Walletstatus(walletstatusId));
		if (wallet.getAccountLoadSequence() < loadSequence) {
			wallet.setAccountLoadSequence(loadSequence);
			wallet.setTouchedBy("lambda-batch");
		}
		walletsRepository.save(wallet);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private Pendingactivity buildPendingactivity(Wallets w, ActivitytypesType type) {
		LOGGER.info("create pending activity for badlist card walletId {}", w.getWalletId());
		Pendingactivity pa = new Pendingactivity();
		Date today = new Date();
		pa.setValueActivity(BigDecimal.ZERO);
		pa.setActivitytypes(new Activitytypes(type.getIndex()));
		pa.setPendingStatus(new ActivityStatuses(READY.getIndex()));
		pa.setDateAdded(today);
		pa.setDateEffective(today);
		pa.setWallets(w);
		pa.setSlot((byte) 0);
		pa.setFarecodes(w.getFarecodes());
		Short pals = pendingActivityRepo.getLoadSequence(w.getWalletId());
		Short apals = pendingActivityRepo.getAccountLoadSequence(w.getWalletId());
		Short wls = (short) 0;
		Short awls = (short) w.getAccountLoadSequence();
		Short maxAccountLoadSequenceNumber = (short) 0;
		maxAccountLoadSequenceNumber = (short) (Math.max((apals == null ? (short) 0 : apals), awls) + 1);
		pa.setAccountLoadSequence(maxAccountLoadSequenceNumber);
		wls = w.getLoadSequence();
		pa.setLoadSequence((short) (Math.max((pals == null ? (short) 0 : pals), wls) + 1));

		Walletorganization wo = null;
		Integer walletOrganizationId = null;
		wo = walletOrgRepo.findByWalletIdFetchOrg(w.getWalletId());
		if (null != wo) {
			walletOrganizationId = wo.getOrganizations().getOrganizationId();
		}
		Activities activity = new Activities(new Activitytypes(type.getIndex()), new ActivityStatuses(READY.getIndex()),
				new Date(), w.getWalletId(), walletOrganizationId, w.getPeople());
		activityRepo.save(activity);
		pa.setCreatedBy("lambda-batch");
		pa.setActivity(activity);
		pendingActivityRepo.save(pa);
		return pa;
	}
}
