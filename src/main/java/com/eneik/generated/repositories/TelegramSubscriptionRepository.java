package com.eneik.generated.repositories;

import com.eneik.generated.models.persistence.TelegramSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramSubscriptionRepository extends JpaRepository<TelegramSubscription, String> {
}
