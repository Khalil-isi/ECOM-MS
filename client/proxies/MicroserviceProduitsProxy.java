package com.client.client.proxies;

import com.client.client.beans.ProduitBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name="zuul-server")
@RibbonClient(name = "microservice-produits")
public interface MicroserviceProduitsProxy {

    @GetMapping(value = "/microservice-produits/produits")
    List<ProduitBean> listeProduits();
    @GetMapping(value = "/microservice-produits/produits/categorie/{categorie}")
    List<ProduitBean> recupererCategorie(@PathVariable("categorie") String categorie);
    @GetMapping( value = "/microservice-produits/produits/{id}")
    ProduitBean recupererProduit(@PathVariable("id") int id);
}
