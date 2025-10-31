/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Banners;
import pe.tarjetaestilos.repositories.BannersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
/**
 *
 * @author julito
 */
@Service
public class BannersService implements IBannersService{
    @Autowired
    private BannersRepository bannersRepository;
    
    @Override
    public Page<Banners> getBanners(Pageable pageable) {
        return this.bannersRepository.findAll(pageable);
    }
    
    public List<Banners> getBannersByMenuSlug(String slug) {
        return bannersRepository.findByMenuSlug(slug);
    }
    
    public Page<Banners> search(
        String title,
        String subtitle,
        Long menuId,
        Boolean blog_form,
        Pageable pageable
    ) {
        return bannersRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (subtitle != null && !subtitle.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("subtitle")), "%" + subtitle.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            
            if (blog_form != null) {
                predicates.add(cb.equal(root.get("blog_form"), blog_form));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<Banners> getBannersAll() {
        return this.bannersRepository.findAll();
    }

    @Override
    public Banners getBannersById(Long id) {
        Banners banners = this.bannersRepository.findById(id).orElse(null);
        return banners;
    }

    @Override
    public Banners saveBanners(Banners banners) {
        return this.bannersRepository.save(banners);
    }

    @Override
    public void deleteBannersById(Long id) {
        this.bannersRepository.deleteById(id);
    }
}
