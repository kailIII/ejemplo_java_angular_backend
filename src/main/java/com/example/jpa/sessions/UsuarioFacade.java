package com.example.jpa.sessions;

import com.example.jpa.entities.TipoDocumento;
import com.example.jpa.entities.TipoDocumento_;
import com.example.jpa.entities.Usuario;
import com.example.jpa.entities.Usuario_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author ruber
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "com.example_BackendEjemploJavaAngular_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    /**
     * Busca usuario por numDocumento
     *
     * @param numDocumento
     * @return Usuario
     */
    public Usuario findUsuarioByNumDocumento(String numDocumento) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = cq.from(Usuario.class);
        cq.where(cb.equal(usuario.get(Usuario_.numDocumento), numDocumento));
        TypedQuery<Usuario> q = getEntityManager().createQuery(cq);
        try {
            return (Usuario) q.getSingleResult();
        } catch (NonUniqueResultException ex) {
            throw ex;
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    /**
     * Busca usuario por email
     *
     * @param email
     * @return Usuario
     */
    public Usuario findUsuarioByEmail(String email) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = cq.from(Usuario.class);
        cq.where(cb.equal(usuario.get(Usuario_.email), email));
        TypedQuery<Usuario> q = getEntityManager().createQuery(cq);
        try {
            return (Usuario) q.getSingleResult();
        } catch (NonUniqueResultException ex) {
            throw ex;
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Usuario> findUsuariosFiltro(int[] rango, String sexo, Boolean activo,
            String tipoDocumento) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = cq.from(Usuario.class);

        Predicate filtro = cb.conjunction();

        if (sexo != null) {
            filtro = cb.and(filtro, cb.equal(usuario.get(Usuario_.sexo), sexo));
        }

        if (activo != null) {
            filtro = cb.and(filtro, cb.equal(usuario.get(Usuario_.activo), activo));
        }

        if (tipoDocumento != null) {
            Join<Usuario, TipoDocumento> joinTipoDocumento = usuario.join(Usuario_.tipoDocumento);
            filtro = cb.and(filtro, cb.equal(joinTipoDocumento.get(TipoDocumento_.id), tipoDocumento));
        }

        cq.where(filtro);
        cq.orderBy(cb.asc(usuario.get(Usuario_.apellidos)));
        TypedQuery<Usuario> tq = em.createQuery(cq);

        if (rango != null) {
            tq.setMaxResults(rango[1] - rango[0] + 1);
            tq.setFirstResult(rango[0]);
        }
        try {
            return tq.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public int countFiltro(String sexo, Boolean activo,
            String tipoDocumento) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Usuario> usuario = cq.from(Usuario.class);

        Predicate filtro = cb.conjunction();

        if (sexo != null) {
            filtro = cb.and(filtro, cb.equal(usuario.get(Usuario_.sexo), sexo));
        }

        if (activo != null) {
            filtro = cb.and(filtro, cb.equal(usuario.get(Usuario_.activo), activo));
        }

        if (tipoDocumento != null) {
            Join<Usuario, TipoDocumento> joinTipoDocumento = usuario.join(Usuario_.tipoDocumento);
            filtro = cb.and(filtro, cb.equal(joinTipoDocumento.get(TipoDocumento_.id), tipoDocumento));
        }

        cq.where(filtro);
        cq.select(cb.count(usuario));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
