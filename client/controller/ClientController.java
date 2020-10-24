package com.client.client.controller;

import com.client.client.beans.CommandeBean;
import com.client.client.beans.PaiementBean;
import com.client.client.beans.ProduitBean;
import com.client.client.proxies.MicroserviceCommandeProxy;
import com.client.client.proxies.MicroservicePaiementProxy;
import com.client.client.proxies.MicroserviceProduitsProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ClientController {
    @Autowired
    MicroserviceProduitsProxy ProduitsProxy;
    @Autowired
    private MicroserviceCommandeProxy CommandesProxy;

    @Autowired
    private MicroservicePaiementProxy PaiementProxy;

    @RequestMapping("/")
    public String accueil(Model model)
    {
        List<ProduitBean> produits = ProduitsProxy.listeProduits();

        model.addAttribute("produits",produits);
        return "index";
    }


    @RequestMapping("/categorie/{categorie}")
    public String categorie(@PathVariable String categorie,Model model)
    {
        List<ProduitBean> produits = ProduitsProxy.recupererCategorie(categorie);

        model.addAttribute("produits",produits);
        return "categorie";
    }

    @RequestMapping("/details-produit/{id}")
    public String ficheProduit(@PathVariable int id, Model model){

        ProduitBean produit = ProduitsProxy.recupererProduit(id);

        model.addAttribute("produit", produit);

        return "FicheProduit";
    }

    @RequestMapping(value = "/commander-produit/{idProduit}/{montant}")
    public String passerCommande(@PathVariable int idProduit, @PathVariable Double montant,  Model model){

        ProduitBean produit = ProduitsProxy.recupererProduit(idProduit);

        model.addAttribute("produit", produit);

        CommandeBean commande = new CommandeBean();

        //On renseigne les propriétés de l'objet
        commande.setProduitId(idProduit);
        commande.setQuantite(1);
        commande.setDateCommande(new Date());

        //appel du microservice commandes
        CommandeBean commandeAjoutee = CommandesProxy.ajouterCommande(commande);

        model.addAttribute("commande", commandeAjoutee);
        model.addAttribute("montant", montant);

        return "Paiement";
    }

    @RequestMapping(value = "/payer-commande/{idCommande}/{montantCommande}")
    public String payerCommande(@PathVariable int idCommande, @PathVariable Double montantCommande, Model model){

        PaiementBean paiementAExcecuter = new PaiementBean();
        CommandeBean commande = CommandesProxy.recupererUneCommande(idCommande);


        paiementAExcecuter.setIdCommande(idCommande);
        paiementAExcecuter.setMontant(montantCommande);
        paiementAExcecuter.setNumeroCarte(numcarte()); // on génère un numéro au hasard

        ResponseEntity<PaiementBean> paiement = PaiementProxy.payerUneCommande(paiementAExcecuter);

        Boolean paiementAccepte = false;

        if(paiement.getStatusCode() == HttpStatus.CREATED) {
            paiementAccepte = true;
            commande.setCommandePayee(true);
            CommandesProxy.ajouterCommande(commande);
        }
        model.addAttribute("paiementOk", paiementAccepte);

        return "confirmation";
    }

    //Génére 16 chiffres(Random) pour simuler le num d'une CB
    private Long numcarte() {

        return ThreadLocalRandom.current().nextLong(1000000000000000L,9000000000000000L );
    }

}

