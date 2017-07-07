package org.springframework.cloud.skipper.server.repository;

import org.springframework.cloud.skipper.domain.Release;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository extends CrudRepository<Release, String>, CustomReleaseRepository {
}
