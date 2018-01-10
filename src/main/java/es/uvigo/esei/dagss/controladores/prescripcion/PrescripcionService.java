/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.controladores.prescripcion;

import java.text.ParseException;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author burni
 */
@Stateless
public class PrescripcionService {
    @Inject    
    private PlanificarRecetas pr;
    
    @EJB
    private PrescripcionDAO prescripcionDAO;
    @EJB
    private RecetaDAO recetaDAO;

    
    public PrescripcionService() {
    }
    
    public void registrarPrescripcion(Prescripcion p) throws ParseException{       
        
        p.setRecetas(pr.calcularRecetas(p));

        prescripcionDAO.anhadirPrescripcion(p);
        
        recetaDAO.anhadirRecetas(p.getRecetas());        
    }
    
}
