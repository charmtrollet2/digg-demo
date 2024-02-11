package org.demo.resources.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.up("Application is up and responding");
  }
}

