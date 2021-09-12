package org.upgrad.upstac.users.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.upgrad.upstac.users.roles.UserRole.*;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void saveRoleFor(UserRole userRole) {
        Role role = new Role();
        role.setName(userRole.name());
        roleRepository.save(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findByRole(UserRole userRole) {

        return roleRepository.findByName(userRole.name());
    }

    public boolean shouldInitialize() {
        return roleRepository.findAll().size() <= 0;
    }

    public Role getForUser() {
        return findByRole(USER);
    }

    public Role getForDoctor() {
        return findByRole(DOCTOR);
    }

    public Role getForTester() {
        return findByRole(TESTER);

    }

    public Role getForGovernmentAuthority() {
        return findByRole(GOVERNMENT_AUTHORITY);

    }

}
