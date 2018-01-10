/*
 Proyecto Java EE, DAGSS-2016
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.*;
import java.util.Date;
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
    
    public List<Prescripcion> buscarPrescripcionesPaciente(Long pacienteId, Date fecha) {
       
        TypedQuery<Prescripcion> q = em.createQuery("SELECT p FROM Prescripcion AS p WHERE p.paciente.id = :pacienteId AND p.fechaFin >= :fecha AND p.fechaInicio <= :fecha", Prescripcion.class); 
        q.setParameter("pacienteId", pacienteId);
        q.setParameter("fecha", fecha); 
        return q.getResultList();
    }
    
    public void anhadirPrescripcion(Prescripcion prescripcion){  
        em.persist(prescripcion);
    }
    
    public void borrarPrescripcion(Prescripcion prescripcion){
        Prescripcion per = em.merge(prescripcion);
        em.remove(per);
    }
    
    public void actualizarPrescripcion(Prescripcion prescripcion){
        em.merge(prescripcion);
    }
    
}
