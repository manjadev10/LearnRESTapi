package com.spring.LearnRESTapi.User;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsCommandLineRunner implements CommandLineRunner{

	
	public UserDetailsCommandLineRunner(UserDetailsRepository repository) {
		super();
		this.repository = repository;
	}

	private UserDetailsRepository repository;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run(String... args) throws Exception {
		
		repository.save(new UserDetails("Manja", "Admin"));
		repository.save(new UserDetails("Maha", "Lead"));
		repository.save(new UserDetails("Laxmi", "Member"));
		
		List<UserDetails> users = repository.findByRole("Admin");
		users.forEach(user -> logger.info(user.toString()));
	}
}
