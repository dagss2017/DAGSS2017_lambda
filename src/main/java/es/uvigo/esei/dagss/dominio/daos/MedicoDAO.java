/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Medico;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
    
    public void actualizarCredenciales(String dni, String nombre, String apellidos, String numeroColegiado, 
            String telefono, String email, String password){
        if(!password.isEmpty()){//si la contraseña no esta vacia la actualizo
            TypedQuery<Medico> q = em.createQuery("UPDATE medico SET PASSWORD=:password WHERE DNI=:dni",Medico.class);
            q.setParameter("password","%"+password+"%" );
            q.getSingleResult();
        }//despues actualizo todos los campos menos el dni y el id que no se pueden actualizar
        TypedQuery<Medico> q = em.createQuery("UPDATE medico SET APELLIDOS=:apellidos, EMAIL=:email, "
                + "NOMBRE=:nombre,NUMEROCOLEGIADO=:numeroColegiado,TELEFONO=:telefono WHERE DNI=:dni",Medico.class);
        q.setParameter("apellidos","%"+apellidos+"%"); 
        q.setParameter("email","%"+email+"%"); 
        q.setParameter("nombre","%"+nombre+"%"); 
        q.setParameter("numeroColegiado","%"+numeroColegiado+"%");
        q.setParameter("telefono","%"+telefono+"%");
        q.setParameter("dni","%"+dni+"%");
        q.getSingleResult();
    }

    // Completar aqui
}
