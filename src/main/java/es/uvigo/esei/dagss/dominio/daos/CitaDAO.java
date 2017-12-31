/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;
import javax.persistence.TypedQuery;


@Stateless
@LocalBean
public class CitaDAO  extends GenericoDAO<Cita>{    

    public List<Cita> buscarTodos() {
        TypedQuery<Cita> q = em.createQuery("SELECT c FROM Cita AS c", Cita.class);
        return q.getResultList();
    }
    
    public List<Cita> buscarCitasPorMedicoDia(Long medicoId, Date fecha) {
       
        TypedQuery<Cita> q = em.createQuery("SELECT c FROM Cita AS c WHERE c.fecha = :fecha AND c.medico.id = :medicoId", Cita.class);
        q.setParameter("fecha", fecha); 
        q.setParameter("medicoId", medicoId);
        
        return q.getResultList();
    }
    
    public void actualizarEstadoCita(Cita cita){
        em.merge(cita);  
    }
}
