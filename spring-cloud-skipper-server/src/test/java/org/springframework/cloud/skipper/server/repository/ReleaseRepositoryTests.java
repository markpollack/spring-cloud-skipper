package org.springframework.cloud.skipper.server.repository;

import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.server.SkipperServerApplication;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SkipperServerApplication.class)
public class ReleaseRepositoryTests {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private ReleaseRepository releaseRepository;

	@Before
	@After
	public void setUp() {

		stringRedisTemplate.execute((RedisConnection connection) -> {
			connection.flushDb();
			return "OK";
		});
	}

	@Test
	public void testReleaseRepository() {
		Release release = new Release();
		release.setName("log");
		release.setVersion(1);
		String text = loadYml("/packages/log.yml");
		release.setManifest(text);

		releaseRepository.save(release);
		Release retrievedRelease = releaseRepository.findOne(release.getId());
		assertThat(retrievedRelease.getName()).isEqualTo("log");
		assertThat(retrievedRelease.getManifest()).isEqualTo(text);
	}

	private String loadYml(String file) {
		return new Scanner(ReleaseRepositoryTests.class.getResourceAsStream(file), "UTF-8")
				.useDelimiter("\\A").next();
	}
}
