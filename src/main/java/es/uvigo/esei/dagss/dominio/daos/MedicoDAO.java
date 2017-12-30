/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class MedicoDAO extends GenericoDAO<Medico> {

    public Medico buscarPorDNI(String dni) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                                            + "  WHERE m.dni = :dni", Medico.class);
        q.setParameter("dni", dni);

        return filtrarResultadoUnico(q);
    }

    public Medico buscarPorNumeroColegiado(String numeroColegiado) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                                            + "  WHERE m.numeroColegiado = :numeroColegiado", Medico.class);
        q.setParameter("numeroColegiado", numeroColegiado);
        
        return filtrarResultadoUnico(q);
    }

    public List<Medico> buscarPorNombre(String patron) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                + "  WHERE (m.nombre LIKE :patron) OR "
                + "        (m.apellidos LIKE :patron)", Medico.class);
        q.setParameter("patron","%"+patron+"%");        
        return q.getResultList();
    }
    
    public void actualizarCredenciales(Medico medico){
        /* em.merge -> actualizar
           em.persist -> insert
           em.remove -> eliminar */
        em.merge(medico);
    }
    
    public List<Cita> buscarCitasPorMedicoDia(Long medicoId, Date fecha) {
       
        TypedQuery<Cita> q = em.createQuery("SELECT c FROM Cita AS c WHERE c.fecha = :fecha AND c.medico.id = :medicoId", Cita.class);
        q.setParameter("fecha", fecha); 
        q.setParameter("medicoId", medicoId);
        
        return q.getResultList();
    }
    
    public List<Prescripcion> buscarPrescripcionesPaciente(Long pacienteId, Date fecha) {
       
        TypedQuery<Prescripcion> q = em.createQuery("SELECT p FROM Prescripcion AS p WHERE p.paciente.id = :pacienteId AND p.fechaFin >= :fecha AND p.fechaInicio <= :fecha", Prescripcion.class); 
        q.setParameter("pacienteId", pacienteId);
        q.setParameter("fecha", fecha); 
        return q.getResultList();
    }
    
    public void anhadirPrescripcion(Prescripcion prescripcion){
        em.persist(prescripcion);
    }
    
    /*public void borrarPrescripcion(Prescripcion prescripcion){
        if (!em.contains(prescripcion)) {
           em.merge(prescripcion);
        }
        em.remove(prescripcion);
    }*/
    
    public void borrarPrescripcion(Prescripcion prescripcion){
        Prescripcion per = em.merge(prescripcion);
        em.remove(per);
    }
    
    public void actualizarPrescripcion(Prescripcion prescripcion){
        em.merge(prescripcion);
    }
    
    // Completar aqui
}
