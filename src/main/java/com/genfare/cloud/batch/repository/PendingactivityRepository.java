package com.genfare.cloud.batch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.genfare.cloud.model.entity.Pendingactivity;

public interface PendingactivityRepository extends CrudRepository<Pendingactivity, Long> {

	@Query("select p from Pendingactivity p where p.wallets.walletId = :walletId and p.pendingStatus.status in (1,2) "
			+ "and p.activitytypes.activityTypeId in (27,28,30) order by p.pendingId asc")
	public List<Pendingactivity> getPendingActivitiesByWalletIdWithStatusAndActivityTypes(
			@Param("walletId") Long walletId);

	@Query("select max(p.loadSequence) from Pendingactivity p where p.wallets.walletId = :walletId")
	public Short getLoadSequence(@Param("walletId") Long walletId);

	@Query("select max(p.accountLoadSequence) from Pendingactivity p where p.wallets.walletId = :walletId")
	public Short getAccountLoadSequence(@Param("walletId") Long walletId);
}
