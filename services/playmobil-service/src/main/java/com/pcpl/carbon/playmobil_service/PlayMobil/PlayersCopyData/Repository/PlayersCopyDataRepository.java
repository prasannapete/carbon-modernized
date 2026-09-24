package com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Repository;

import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.Model.PlayersCopyData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface PlayersCopyDataRepository extends JpaRepository<PlayersCopyData,String> {
}
