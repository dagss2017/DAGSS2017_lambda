/*
 Proyecto Java EE, DAGSS-2016
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.*;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class PrescripcionDAO extends GenericoDAO<Prescripcion> {

    public Prescripcion buscarPorIdConRecetas(Long id) {
        TypedQuery<Prescripcion> q = em.createQuery("SELECT p FROM Prescripcion AS p JOIN FETCH p.recetas"
                                                  + "  WHERE p.id = :id", Prescripcion.class);
        q.setParameter("id", id);
        
        return q.getSingleResult();
    }
    
    public List<Prescripcion> buscarPorPaciente(String dni){
        
        TypedQuery<Prescripcion> q = em.createQuery("SELECT p FROM Prescripcion p  JOIN FETCH p.recetas"
                                                  + "  WHERE p.paciente.dni = :DNI", Prescripcion.class);
                q.setParameter("DNI",dni);

        return q.getResultList();
    }
    
    // Completar aqui  
}
