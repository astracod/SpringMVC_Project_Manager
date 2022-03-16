package org.example.springtask.config.security;

import lombok.RequiredArgsConstructor;
import org.example.springtask.entity.Worker;
import org.example.springtask.repository.interfaces.WorkerDAO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final WorkerDAO workerDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Worker worker = workerDAO.getWorkerByEmail(username);
        return SecurityUser.fromUser(worker);
    }
}
