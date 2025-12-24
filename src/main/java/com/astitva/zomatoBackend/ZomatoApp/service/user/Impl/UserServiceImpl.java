package com.astitva.zomatoBackend.ZomatoApp.service.user.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.LogoutResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.Address;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.repository.SessionRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SessionRepository sessionRepository;

    private void validateAdmin(Long adminUserId) {
        User user = loadUserEntity(adminUserId);
        if(!user.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admin can perform this action");
        }
    }

    @Override
    public User loadUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId, Long requesterUserId) {
        User requester = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // User can view only their own profile OR admin can
        if (!requester.getId().equals(user.getId()) &&
                !requester.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Not allowed to view this user");
        }

        UserResponse response = modelMapper.map(user, UserResponse.class);

        List<AddressResponse> addressResponses = user.getAddresses()
                .stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();

        response.setAddresses(addressResponses);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Pageable pageable, Long adminUserId) {
        validateAdmin(adminUserId);

        List<User> usersPage = userRepository.findAll(pageable).getContent();

        return usersPage.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();

//        return usersPage.map(user -> {
//            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
//            List<Address> addressResponseList = user.getAddresses()
//                    .stream()
//                    .map(address -> modelMapper.map(address, Address.class))
//                    .toList();
//
//            userResponse.setAddresses(addressResponseList);
//            return userResponse;
//        });
    }

    @Override
    public LogoutResponse deleteUser(Long userId, Long adminUserId) {
        validateAdmin(adminUserId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(Objects.equals(user.getId(), adminUserId)) {
            throw new UnauthorizedException("Admin cannot be deleted");
        }

        sessionRepository.deleteAll(sessionRepository.findByUser(user));
        userRepository.delete(user);

        return new LogoutResponse("User deleted successfully.");
    }

    @Override
    public void updateUserRole(Long userId, UserRole newRole, Long adminUserId) {

        validateAdmin(adminUserId);

        if (newRole == UserRole.ADMIN) {
            throw new UnauthorizedException("Creating a new admin is not allowed.");
        }

    }

    @Override
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable, Long adminUserId) {
        return null;
    }
}
