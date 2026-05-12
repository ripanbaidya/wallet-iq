package online.walletiq.service.impl;

import online.walletiq.config.properties.ApplicationProperties;
import online.walletiq.dto.app.AppInfoResponse;
import online.walletiq.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationProperties appProperties;

    @Override
    public AppInfoResponse getAppInfo() {
        return AppInfoResponse.builder()
            .name(appProperties.name())
            .version(appProperties.version())
            .buildNumber(appProperties.buildNumber())
            .serverTime(Instant.now())
            .copyright(appProperties.copyright())
            .license(new AppInfoResponse.License(
                appProperties.license().name(),
                appProperties.license().url()
            ))
            .support(new AppInfoResponse.Support(
                appProperties.support().email(),
                appProperties.support().workingHours()
            ))
            .social(new AppInfoResponse.Social(
                appProperties.social().github()
            ))
            .build();
    }
}
