// MahasiswaController.java 
package com.example.controller; 
 
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.jdbc.core.BeanPropertyRowMapper; 
import org.springframework.jdbc.core.JdbcTemplate; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping; 
import com.example.model.Mahasiswa; 

@Controller
public class MahasiswaController { 
    @Autowired 
    private JdbcTemplate jdbcTemplate; 

    @GetMapping("/") 
    public String index(Model model) { 
        String sql = "SELECT * FROM mahasiswa"; 
        List<Mahasiswa> mahasiswa = jdbcTemplate.query(sql, 
            BeanPropertyRowMapper.newInstance(Mahasiswa.class)); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "index"; 
    }

    @GetMapping("/add")
    public String add(Model model) { 
        model.addAttribute("mahasiswa", new Mahasiswa());
        return "add"; 
    }

 
    @PostMapping("/add") 
    public String add(Mahasiswa mahasiswa) { 
        String sql = "INSERT INTO mahasiswa (nim, nama, angkatan, gender) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, mahasiswa.getNim(), 
                mahasiswa.getNama(), 
mahasiswa.getAngkatan(), mahasiswa.getGender()); 
        return "redirect:/"; 
    }   

    @GetMapping("/edit/{nim}") 
    public String edit(@PathVariable("nim") String nim, Model 
model) { 
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?"; 
        Mahasiswa mahasiswa = 
jdbcTemplate.queryForObject(sql, 
BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "edit"; 
    } 
 
    @PostMapping("/edit") 
    public String edit(Mahasiswa mahasiswa) { 
        String sql = "UPDATE mahasiswa SET nama = ?, angkatan = ?, gender = ? WHERE nim = ?"; 
        jdbcTemplate.update(sql, mahasiswa.getNama(), 
mahasiswa.getAngkatan(), mahasiswa.getGender(), 
                mahasiswa.getNim()); 
        return "redirect:/"; 
    } 

    @GetMapping("/delete/{nim}") 
    public String delete(@PathVariable("nim") String nim) { 
        String sql = "DELETE FROM mahasiswa WHERE nim = ?"; 
        jdbcTemplate.update(sql, nim); 
        return "redirect:/"; 
    } 

    @GetMapping("/detail/{nim}")
public String detail(@PathVariable("nim") String nim, Model model) {
    // Mengambil data mahasiswa
    String sqlMahasiswa = "SELECT * FROM mahasiswa WHERE nim = ?";
    Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sqlMahasiswa, BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
    
    // Mengambil mata kuliah yang diambil mahasiswa
    String sqlIRS = "SELECT m.matkul_nama, m.sks, m.hari, i.status FROM irs i " +
                    "JOIN matakuliah m ON i.matkul_id = m.matkul_id " +
                    "WHERE i.nim = ?";
    List<Map<String, Object>> matkulList = jdbcTemplate.queryForList(sqlIRS, nim);
    
    model.addAttribute("mahasiswa", mahasiswa);
    model.addAttribute("matkulList", matkulList);
    
    return "detail";
}

}
