package com.genfare.cloud.batch.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.genfare.cloud.model.entity.Walletorganization;
import com.genfare.cloud.model.entity.WalletorganizationId;

public interface WalletOrganizationRepository extends CrudRepository<Walletorganization, WalletorganizationId> {

	@Query("from Walletorganization wo join fetch wo.organizations where wo.wallets.walletId = :walletId")
	public Walletorganization findByWalletIdFetchOrg(@Param("walletId") Long walletId);

}
