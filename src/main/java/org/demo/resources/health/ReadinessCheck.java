package org.demo.resources.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {
    // 'Another' eftersom det finns en inbakad ready-check på Postgres redan
    return HealthCheckResponse.up("Another readiness check");
  }
}
