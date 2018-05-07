package com.github.dapeng.api.gateway.repository;

import com.github.dapeng.api.gateway.dto.AuthInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author with struy.
 * Create by 2018/3/29 19:39
 * email :yq1724555319@gmail.com
 */

@Repository
public interface GateWayRepository extends CrudRepository<AuthInfo, String> {
    AuthInfo findByAuthKey(String authkey);
}
