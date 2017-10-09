package com.example.rest.services;

import com.example.jpa.entities.TipoDocumento;
import com.example.jpa.sessions.TipoDocumentoFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ruber
 */
@Path("tipos_documentos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TipoDocumentoREST {
    
    @EJB
    private TipoDocumentoFacade tipoDocumentoEJB;
    
    @GET
    public List<TipoDocumento> findAll(){
        return tipoDocumentoEJB.findAll();
    }
    
}
