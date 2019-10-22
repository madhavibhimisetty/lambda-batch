//package com.genfare.cloud.batch.service;
//
//import static com.genfare.cloud.core.types.WalletstatusType.ACTIVE;
//import static com.genfare.cloud.core.types.WalletstatusType.BAD_LISTED;
//import static org.junit.Assert.assertEquals;
//
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.when;
//
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.reflect.Whitebox;
//
//import com.genfare.cloud.core.types.AccountType;
//import com.genfare.cloud.core.types.ActivityStatusType;
//import com.genfare.cloud.core.types.ActivitytypesType;
//import com.genfare.cloud.core.types.WalletstatusType;
//import com.genfare.cloud.core.types.WallettypesType;
//import com.genfare.cloud.model.entity.Activitytypes;
//import com.genfare.cloud.model.entity.Farecodes;
//import com.genfare.cloud.model.entity.Pendingactivity;
//import com.genfare.cloud.model.entity.Walletidentifiers;
//import com.genfare.cloud.model.entity.WalletidentifiersId;
//import com.genfare.cloud.model.entity.Wallets;
//import com.genfare.cloud.model.entity.Walletstatus;
//import com.genfare.cloud.model.entity.Wallettypes;
//import com.genfare.cloud.model.repository.ActivitiesRepository;
//import com.genfare.cloud.model.repository.PendingactivityRepository;
//import com.genfare.cloud.model.repository.WalletOrganizationRepository;
//import com.genfare.cloud.model.repository.WalletsRepository;
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ BadListAndGoodListService.class, BadListAndGoodListServiceImpl.class })
//public class BadListAndGoodListServiceImplTest {
//
//    @Mock
//    private WalletsRepository walletsRepository;
//
//    @Mock
//    private PendingactivityRepository pendingActivityRepo;
//
//    @Mock
//    private WalletOrganizationRepository walletOrgRepo;
//
//    @Mock
//    private ActivitiesRepository activityRepo;
//
//    @InjectMocks
//    @Spy
//    BadListAndGoodListServiceImpl badListAndGoodListService = new BadListAndGoodListServiceImpl();
//
//    private static final Byte ACTIVE_WALLET = ACTIVE.getIndex();
//    private static final Byte BADLIST_WALLET = BAD_LISTED.getIndex();
//
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    @Test
//    public void testCreateGoodListAndBadListToBadListCard() throws Exception {
//        Wallets wallet = buildWallet(584L);
//        when(walletsRepository.getAccountBasedCardToBadlist()).thenReturn(Arrays.asList(new BigInteger("584")));
//        when(walletsRepository.getAccountBasedCardToGoodlist()).thenReturn(new ArrayList<BigInteger>());
//        when(walletsRepository.findOne(584L)).thenReturn(wallet);
//        when(pendingActivityRepo.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(Mockito.any(Long.class))).thenReturn(new ArrayList<Pendingactivity>());
//        badListAndGoodListService.createGoodListAndBadList();
//        PowerMockito.verifyPrivate(badListAndGoodListService, times(1)).invoke("updateWallet", wallet, BADLIST_WALLET, new Short((short) 1));
//    }
//
//    @Test
//    public void testCreateGoodListAndBadListToGoodListCard() throws Exception {
//        Wallets wallet = buildWallet(814L);
//        when(walletsRepository.getAccountBasedCardToBadlist()).thenReturn(new ArrayList<BigInteger>());
//        when(walletsRepository.getAccountBasedCardToGoodlist()).thenReturn(Arrays.asList(new BigInteger("814")));
//        when(walletsRepository.findOne(814L)).thenReturn(wallet);
//        when(pendingActivityRepo.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(Mockito.any(Long.class))).thenReturn(buildPendingactivity());
//        badListAndGoodListService.createGoodListAndBadList();
//        PowerMockito.verifyPrivate(badListAndGoodListService, times(1)).invoke("updateWallet", wallet, ACTIVE_WALLET, new Short((short) 1));
//    }
//
//    @Test
//    public void testCancelExistingPendingActivty() throws Exception {
//        Wallets wallet = buildWallet(584L);
//        when(pendingActivityRepo.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(Mockito.any(Long.class))).thenReturn(buildPendingactivity());
//        Pendingactivity pendingactivity = Whitebox.invokeMethod(badListAndGoodListService, "cancelExistingPendingActivty", wallet);
//        assertNotNull(pendingactivity);
//        assertEquals(pendingactivity.getPendingStatus().getStatus(), ActivityStatusType.CANCELED.getIndex());
//    }
//
//    @Test
//    public void testCreateOrUpdatePendingActivityForGoodList() throws Exception {
//        Wallets wallet = buildWallet(584L);
//        ActivitytypesType accountGoodList = ActivitytypesType.ACCOUNT_GOOD_LIST;
//        when(pendingActivityRepo.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(Mockito.any(Long.class))).thenReturn(buildPendingactivity());
//        Pendingactivity pendingactivity = Whitebox.invokeMethod(badListAndGoodListService, "createOrUpdatePendingActivity", wallet, accountGoodList);
//        assertNotNull(pendingactivity);
//        assertEquals(pendingactivity.getPendingStatus().getStatus(), ActivityStatusType.CANCELED.getIndex());
//    }
//
//    @Test
//    public void testCreateOrUpdatePendingActivityForBadList() throws Exception {
//        Wallets wallet = buildWallet(584L);
//        ActivitytypesType accountBadList = ActivitytypesType.ACCOUNT_BAD_LIST;
//        when(pendingActivityRepo.getPendingActivitiesByWalletIdWithStatusAndActivityTypes(Mockito.any(Long.class))).thenReturn(new ArrayList<Pendingactivity>());
//        Pendingactivity pendingactivity = Whitebox.invokeMethod(badListAndGoodListService, "createOrUpdatePendingActivity", wallet, accountBadList);
//        assertNotNull(pendingactivity);
//        assertNotEquals(pendingactivity.getPendingStatus().getStatus(), ActivityStatusType.CANCELED.getIndex());
//        assertEquals(pendingactivity.getPendingStatus().getStatus(), ActivityStatusType.READY.getIndex());
//    }
//
//    private Wallets buildWallet(Long walletId) {
//        Wallets w = new Wallets();
//        w.setWalletId(walletId);
//        w.setFarecodes(new Farecodes());
//        w.setLoadSequence((short) 0);
//        w.setWalletidentifierses(buildIdentifiers(1L));
//        w.setWalletstatus(new Walletstatus(WalletstatusType.ACTIVE.getIndex()));
//        w.setWallettypes(new Wallettypes(WallettypesType.ACCOUNT_BASED.getIndex()));
//        w.setAccountType((AccountType.ACCOUNT_BASED.getDescription()));
//        return w;
//    }
//
//    private Set<Walletidentifiers> buildIdentifiers(long id) {
//        Set<Walletidentifiers> identifiers = new HashSet<Walletidentifiers>();
//
//        Walletidentifiers electricId = new Walletidentifiers();
//        electricId.setId(new WalletidentifiersId(id, (byte) 1));
//        electricId.setIdentifier("00001");
//
//        Walletidentifiers printedId = new Walletidentifiers();
//        printedId.setId(new WalletidentifiersId(id, (byte) 2));
//        printedId.setIdentifier("1234");
//
//        identifiers.add(electricId);
//        identifiers.add(printedId);
//        return identifiers;
//    }
//
//    public List<Pendingactivity> buildPendingactivity() {
//        Pendingactivity pa = null;
//        Activitytypes type = null;
//        List<Pendingactivity> activities = new ArrayList<Pendingactivity>();
//        pa = new Pendingactivity();
//        pa.setPendingId(1l);
//        pa.setAccountLoadSequence((short) 1);
//        type = new Activitytypes();
//        type.setDescription("ACCOUNT BAD LIST");
//        pa.setActivitytypes(type);
//        pa.setWallets(buildWallet(814L));
//        activities.add(pa);
//        return activities;
//    }
//}
