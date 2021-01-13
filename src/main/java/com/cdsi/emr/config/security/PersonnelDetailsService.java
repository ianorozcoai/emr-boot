package com.cdsi.emr.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cdsi.emr.personnel.PersonnelRepository;

@Service
public class PersonnelDetailsService implements UserDetailsService {
	
	private PersonnelRepository personnelRepository;
	
	public PersonnelDetailsService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username != null) {
            if (username.indexOf("@") != -1) {
                return this.personnelRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Email not found."));
            } else {
                return this.personnelRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found."));
            }
        }
        throw new UsernameNotFoundException("Username not found.");
	}

}
