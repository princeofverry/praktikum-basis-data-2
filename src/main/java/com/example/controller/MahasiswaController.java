// MahasiswaController.java
package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.model.Mahasiswa;

@Controller
public class MahasiswaController { 
    @Autowired 
    private JdbcTemplate jdbcTemplate; 

    @GetMapping("/")
    public String index(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        String sql;
        List<Mahasiswa> mahasiswa;
        List<Mahasiswa> deletedMahasiswa;
    
        if (keyword != null && !keyword.isEmpty()) {
            sql = "SELECT * FROM mahasiswa WHERE (nama LIKE ? OR nim LIKE ?) AND deleted_at IS NULL";
            mahasiswa = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class), "%" + keyword + "%", "%" + keyword + "%");
        } else {
            sql = "SELECT * FROM mahasiswa WHERE deleted_at IS NULL";
            mahasiswa = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class));
        }
    
        // Ambil data yang sudah dihapus
        String deletedSql = "SELECT * FROM mahasiswa WHERE deleted_at IS NOT NULL";
        deletedMahasiswa = jdbcTemplate.query(deletedSql, BeanPropertyRowMapper.newInstance(Mahasiswa.class));
    
        model.addAttribute("mahasiswa", mahasiswa);
        model.addAttribute("deletedMahasiswa", deletedMahasiswa);
        model.addAttribute("keyword", keyword);
    
        return "index";
    }
    

    @GetMapping("/add")
    public String add(Model model) { 
        model.addAttribute("mahasiswa", new Mahasiswa());
        return "add"; 
    }

    @PostMapping("/add")
    public String add(Mahasiswa mahasiswa, Model model) {
        String checkSql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, mahasiswa.getNim());

        if (count != null && count > 0) {
            model.addAttribute("error", "NIM sudah terdaftar!");
            model.addAttribute("mahasiswa", mahasiswa);
            return "add";
        }

        String sql = "INSERT INTO mahasiswa (nim, nama, angkatan, gender, deleted_at) VALUES (?, ?, ?, ?, NULL)";
        jdbcTemplate.update(sql, mahasiswa.getNim(), mahasiswa.getNama(), mahasiswa.getAngkatan(), mahasiswa.getGender());
        
        return "redirect:/";
    }

    @GetMapping("/edit/{nim}") 
    public String edit(@PathVariable("nim") String nim, Model model) { 
        String sql = "SELECT * FROM mahasiswa WHERE nim = ? AND deleted_at IS NULL"; 
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "edit"; 
    }

    @PostMapping("/edit") 
    public String edit(Mahasiswa mahasiswa) { 
        String sql = "UPDATE mahasiswa SET nama = ?, angkatan = ?, gender = ? WHERE nim = ? AND deleted_at IS NULL"; 
        jdbcTemplate.update(sql, mahasiswa.getNama(), mahasiswa.getAngkatan(), mahasiswa.getGender(), mahasiswa.getNim()); 
        return "redirect:/"; 
    }

    // Soft Delete: Update deleted_at, bukan delete dari database
    @GetMapping("/delete/{nim}") 
    public String softDelete(@PathVariable("nim") String nim) { 
        String sql = "UPDATE mahasiswa SET deleted_at = ? WHERE nim = ?"; 
        jdbcTemplate.update(sql, LocalDateTime.now(), nim); // Set waktu saat ini sebagai tanda soft delete
        return "redirect:/"; 
    } 

    // Restore data yang sudah dihapus
    @GetMapping("/restore/{nim}") 
    public String restore(@PathVariable("nim") String nim) { 
        String sql = "UPDATE mahasiswa SET deleted_at = NULL WHERE nim = ?"; 
        jdbcTemplate.update(sql, nim); // Hapus timestamp deleted_at agar data kembali aktif
        return "redirect:/"; 
    } 
}
