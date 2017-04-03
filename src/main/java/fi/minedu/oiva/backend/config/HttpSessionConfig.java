package fi.minedu.oiva.backend.config;

import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Profile({"!test"})
@EnableRedisHttpSession
public class HttpSessionConfig { }
