package com.genfare.cloud.batch.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.genfare.cloud.model.entity.Wallets;

public interface WalletsRepository extends CrudRepository<Wallets, Long> {

	@Query(value = " select distinct w.walletid from people p " + " join accountbalance ab on p.personid = ab.personid "
			+ " join Wallets w on w.personid = p.personid " + " join groupsofpeople gop on gop.personid = p.personid "
			+ " join Groups g on g.groupid = gop.groupid "
			+ " join organizations o on g.OrganizationID = o.OrganizationID "
			+ " where ab.amount < IFNULL(o.gst, (select o.gst from Organizations o where o.isAgency = 't')) "
			+ " and w.walletStatusId = 2 and w.walletTypeId != 5 and w.accountType = 'Account-Based' and g.groupId not in (19) ", nativeQuery = true)
	public List<BigInteger> getAccountBasedCardToBadlist();

	@Query(value = " select distinct w.walletid from people p " + " join accountbalance ab on p.personid = ab.personid "
			+ " join Wallets w on w.personid = p.personid " + " join groupsofpeople gop on gop.personid = p.personid "
			+ " join Groups g on g.groupid = gop.groupid "
			+ " join organizations o on g.OrganizationID = o.OrganizationID "
			+ " where ab.amount >= IFNULL(o.gst, (select o.gst from Organizations o where o.isAgency = 't')) "
			+ " and w.walletStatusId = 17 and w.walletTypeId != 5 and w.accountType = 'Account-Based' and g.groupId not in (19) ", nativeQuery = true)
	public List<BigInteger> getAccountBasedCardToGoodlist();
}
