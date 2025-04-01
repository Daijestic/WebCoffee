package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "NhanVien")
public class NhanVienEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "ChucVu")
    private String chucVu;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "Email")
    private String email;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TaiKhoan")
    private TaiKhoanEntity taiKhoan;

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDonEntity> hoaDon;

    @OneToMany(mappedBy = "nhanVien")
    private List<CaLamViecEntity> listCaLamViec;

    @OneToMany(mappedBy = "nhanVien")
    private List<PhieuNhapKhoEntity> listPhieuNhapKho;

    @OneToMany(mappedBy = "nhanVien")
    private List<PhieuXuatKhoEntity> listPhieuXuatKho;

    public TaiKhoanEntity getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoanEntity taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<HoaDonEntity> getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(List<HoaDonEntity> hoaDon) {
        this.hoaDon = hoaDon;
    }

    public List<CaLamViecEntity> getListCaLamViec() {
        return listCaLamViec;
    }

    public void setListCaLamViec(List<CaLamViecEntity> listCaLamViec) {
        this.listCaLamViec = listCaLamViec;
    }

    public List<PhieuNhapKhoEntity> getListPhieuNhapKho() {
        return listPhieuNhapKho;
    }

    public void setListPhieuNhapKho(List<PhieuNhapKhoEntity> listPhieuNhapKho) {
        this.listPhieuNhapKho = listPhieuNhapKho;
    }

    public List<PhieuXuatKhoEntity> getListPhieuXuatKho() {
        return listPhieuXuatKho;
    }

    public void setListPhieuXuatKho(List<PhieuXuatKhoEntity> listPhieuXuatKho) {
        this.listPhieuXuatKho = listPhieuXuatKho;
    }
}
