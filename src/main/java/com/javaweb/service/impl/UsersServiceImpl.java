package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.AddToCartDtoToEntity;
import com.javaweb.converter.dto_to_entity.UserRequestToEntity;
import com.javaweb.converter.entity_to_dto.ChiTietGioHangEntityToDto;
import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.dto.reponse.CartResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.enums.Role;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.*;
import com.javaweb.service.UsersService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.modelmapper.Converters.Collection.map;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaiKhoanRespository taiKhoanRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    private UserEntityToDTO userEntityToDTO;

    @Autowired
    private UserRequestToEntity userRequestToEntity;

    @Autowired
    private AddToCartDtoToEntity addToCartDtoToEntity;

    @Autowired
    private ChiTietGioHangEntityToDto chiTietGioHangEntityToDto;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (taiKhoanRespository.existsByDangNhap(userRequest.getUsername())) {
            throw new ApplicationException(ErrorCode.USER_EXIST);
        }

        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);

        userEntity.setMatKhau(passwordEncoder.encode(userRequest.getPassword()));
        String role = new String();
        if (userRequest.getRoles().isEmpty()) {
            role = Role.USER.name();
        } else {
            role = Optional.ofNullable(userRequest.getRoles())
                    .filter(roles -> !roles.isEmpty())
                    .map(roles -> roles.iterator().next())
                    .orElse("ROLE_USER"); // Giá trị mặc định

        }
        userEntity.setLoaiUser(role);

        userRepository.save(userEntity);

        return userEntityToDTO.UserEntityToDTO(userEntity);
    }

    @Override
    public List<UserResponse> findAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
            Set<String> roles = new HashSet<>();
            roles.add(userResponse.getRole());
            userResponse.setRoles(roles);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public UserResponse findById(Long id) {
        return userEntityToDTO.UserEntityToDTO(userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ApplicationException(ErrorCode.CLIENT_NOT_EXIST);
        }
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        UserEntity userEntity = taiKhoanRespository.findByDangNhap(username).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_EXIST)
        );
        return userEntityToDTO.UserEntityToDTO(userEntity);
    }

    @Override
    public UserResponse update(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserEntity userEntity = userRequestToEntity.userRequestToEntity(userRequest);
        return userEntityToDTO.UserEntityToDTO(userRepository.save(userEntity));
    }

    @Override
    public void deleteKhachHangById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserResponse> findAll(Integer pageNo) {

        Pageable pageable = PageRequest.of(pageNo - 1, 13);

        return this.userRepository.findAll(pageable)
                .map(userEntity -> {
                    UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
                    Set<String> roles = new HashSet<>();
                    roles.add(userResponse.getRole());
                    userResponse.setRoles(roles);
                    return userResponse;
                });
    }

    @Override
    public Page<UserResponse> findAllByRole(Integer pageNo, String role) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return this.userRepository.findAllByLoaiUser(role, pageable)
                .map(userEntity -> {
                    UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
                    Set<String> roles = new HashSet<>();
                    roles.add(userResponse.getRole());
                    userResponse.setRoles(roles);
                    return userResponse;
                });
    }

    @Override
    public UserResponse findByUsername(String username) {
        return userRepository.findByDangNhap(username)
                .map(userEntity -> {
                    UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
                    Set<String> roles = new HashSet<>();
                    roles.add(userResponse.getRole());
                    userResponse.setRoles(roles);
                    return userResponse;
                }).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
    }

    @Override
    public List<UserResponse> findAllByRole(String role) {
        return userRepository.findAllByLoaiUser(role)
                .stream()
                .map(userEntity -> {
                    UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
                    Set<String> roles = new HashSet<>();
                    roles.add(userResponse.getRole());
                    userResponse.setRoles(roles);
                    return userResponse;
                }).toList();
    }

    @Override
    public UserResponse themVaoGioHang(AddToCartRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
        userEntity.getChiTietGioHangEntityList().add(addToCartDtoToEntity.convertToEntity(request));
        return userEntityToDTO.UserEntityToDTO(userEntity);
    }

    @Override
    public Long countByUserId(Long userId) {
        return (long) userRepository.findByIdUser(userId).get().getChiTietGioHangEntityList().size();
    }

    @Override
    public List<CartResponse> layDanhSachGioHang(Long userId) {
        return userRepository.findByIdUser(userId).get().getChiTietGioHangEntityList()
                .stream().map(chiTietGioHangEntity -> {
                    return chiTietGioHangEntityToDto.convertToDto(chiTietGioHangEntity);
                }).toList();
    }

    @Override
    public void capNhatSoLuong(AddToCartRequest request) {
        chiTietGioHangRepository.save(addToCartDtoToEntity.convertToEntity(request));
    }

    @Override
    public void xoaSanPhamKhoiGioHang(AddToCartRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
        userEntity.getChiTietGioHangEntityList().remove(addToCartDtoToEntity.convertToEntity(request));
    }

    @Override
    public void xoaGioHang(Long userId) {
        // Tìm người dùng theo userId
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXIST));

        // Lấy danh sách chi tiết giỏ hàng của người dùng
        List<ChiTietGioHangEntity> chiTietGioHangList = userEntity.getChiTietGioHangEntityList();

        // Xóa tất cả các chi tiết giỏ hàng trong cơ sở dữ liệu
        chiTietGioHangRepository.deleteAll(chiTietGioHangList);

        // Xóa các chi tiết giỏ hàng khỏi danh sách của người dùng (không cần thiết với CascadeType.ALL)
        chiTietGioHangList.clear();

        // Cập nhật lại người dùng
        userRepository.save(userEntity);
    }


    @Autowired
    private MonRepository monRepository;

    @Autowired
    private SizeRepository sizeRepository;


    @Override
    @Transactional
    public boolean addToCart(AddToCartRequest request, String username) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (username == null || username.isEmpty()) {
                System.err.println("Username trống");
                return false;
            }

            if (request == null) {
                System.err.println("Request trống");
                return false;
            }

            if (request.getMonId() == null) {
                System.err.println("MonId không được để trống");
                return false;
            }

            if (request.getSizeId() == null) {
                System.err.println("SizeId không được để trống");
                return false;
            }

            if (request.getSoLuong() <= 0) {
                System.err.println("Số lượng phải lớn hơn 0");
                return false;
            }

            // Tìm người dùng theo username
            UserEntity user = userRepository.findByDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // In ra ID người dùng để kiểm tra
            System.out.println("ID người dùng: " + user.getIdUser());

            // Tìm món và size
            MonEntity mon = monRepository.findById(request.getMonId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món"));

            SizeEntity size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));

            // TẠO ĐÚNG CHiTietGioHangId với ID của người dùng
            ChiTietGioHangEntity.ChiTietGioHangId cartId =
                    new ChiTietGioHangEntity.ChiTietGioHangId(
                            request.getMonId(),
                            request.getSizeId(),
                            user.getIdUser()  // Đảm bảo idUser được đưa vào đúng
                    );

            System.out.println("ID giỏ hàng: monId=" + cartId.getMonId() +
                    ", sizeId=" + cartId.getSizeId() +
                    ", userId=" + cartId.getUserId());

            // Kiểm tra xem đã có sản phẩm này trong giỏ hàng chưa
            Optional<ChiTietGioHangEntity> existingCartItem =
                    chiTietGioHangRepository.findByIdMonIdAndIdSizeIdAndIdUserId(
                            request.getMonId(), request.getSizeId(), user.getIdUser());

            if (existingCartItem.isPresent()) {
                // Nếu đã có, cập nhật số lượng
                ChiTietGioHangEntity cartItem = existingCartItem.get();
                cartItem.setSoLuong(cartItem.getSoLuong() + request.getSoLuong());
                cartItem.setGhiChu(request.getGhiChu());
                ChiTietGioHangEntity saved = chiTietGioHangRepository.save(cartItem);
                System.out.println("Cập nhật giỏ hàng thành công: " + saved.getId());
            } else {
                // Nếu chưa có, tạo mới
                ChiTietGioHangEntity newCartItem = new ChiTietGioHangEntity();
                newCartItem.setId(cartId);
                newCartItem.setMon(mon);
                newCartItem.setSize(size);
                newCartItem.setSoLuong(request.getSoLuong());
                newCartItem.setGhiChu(request.getGhiChu());
                newCartItem.setUser(user);

                ChiTietGioHangEntity saved = chiTietGioHangRepository.save(newCartItem);
                System.out.println("Thêm mới giỏ hàng thành công: " + saved.getId());
            }

            return true;
        } catch (Exception e) {
            System.err.println("Lỗi chi tiết khi thêm vào giỏ hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public Long getUserIdByUsername(String username) {
        Optional<UserEntity> userOptional = userRepository.findByDangNhap(username);
        return userOptional.isPresent() ? userOptional.get().getIdUser() : null;
    }
}