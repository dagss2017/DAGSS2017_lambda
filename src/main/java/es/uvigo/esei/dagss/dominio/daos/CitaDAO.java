/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
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
}
