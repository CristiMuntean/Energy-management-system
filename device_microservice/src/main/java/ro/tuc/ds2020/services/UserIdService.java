package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.UserIdDTO;
import ro.tuc.ds2020.dtos.builders.UserIdBuilder;
import ro.tuc.ds2020.entities.UserId;
import ro.tuc.ds2020.repositories.UserIdRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserIdService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserIdService.class);
    private final UserIdRepository userIdRepository;

    public UserIdService(UserIdRepository userIdRepository) {
        this.userIdRepository = userIdRepository;
    }

    public List<UserIdDTO> findUserIds() {
        List<UserId> userIdList = userIdRepository.findAll();
        return userIdList.stream()
                .map(UserIdBuilder::toUserIdDTO)
                .collect(Collectors.toList());
    }

    public UserIdDTO findUserIdById(Long id) {
        UserId userId = userIdRepository.findById(id).orElse(null);
        return UserIdBuilder.toUserIdDTO(userId);
    }

    public Long insert(UserIdDTO userIdDTO) {
        UserId userId = UserIdBuilder.toEntity(userIdDTO);
        userId = userIdRepository.save(userId);
        LOGGER.debug("UserId with id {} was inserted in db", userId.getUserId());
        return userId.getUserId();
    }

    public Long delete(UserIdDTO userIdDTO) {
        UserId userId = UserIdBuilder.toEntity(userIdDTO);
        userIdRepository.delete(userId);
        LOGGER.debug("UserId with id {} was deleted from the db", userId.getUserId());
        return userId.getUserId();
    }
}
