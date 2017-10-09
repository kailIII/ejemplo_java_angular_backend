package com.example.rest.services;

import com.example.jpa.entities.Usuario;
import com.example.jpa.sessions.UsuarioFacade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ruber
 */
@Path("usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioREST {

    @EJB
    private UsuarioFacade usuarioEJB;

    @GET
    public List<Usuario> findAll(
            @QueryParam("desde") Integer desde,
            @QueryParam("hasta") Integer hasta,
            @QueryParam("sexo") String sexo,
            @QueryParam("activo") Boolean activo,
            @QueryParam("tipoDocumento") String tipoDocumento) {

        return usuarioEJB.findUsuariosFiltro((desde != null && hasta != null ? new int[]{desde, hasta} : null),
                sexo, activo, tipoDocumento);
    }

    @POST
    public Response create(Usuario usuario) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try {
            if (usuarioEJB.findUsuarioByNumDocumento(usuario.getNumDocumento()) == null) {

                if (usuarioEJB.findUsuarioByEmail(usuario.getEmail()) == null) {
                    usuario.setActivo(Boolean.TRUE);
                    usuarioEJB.create(usuario);
                    return Response.status(Response.Status.CREATED).entity(gson.toJson("El usuario se registro correctamente!")).build();

                } else {
                    return Response.status(Response.Status.CONFLICT).entity(gson.toJson("El email ya esta registrado!")).build();
                }

            } else {
                return Response.status(Response.Status.CONFLICT).entity(gson.toJson("El número de documento ya esta registrado!")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson("Error de persistencia!")).build();
        }
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST(
            @QueryParam("sexo") String sexo,
            @QueryParam("activo") Boolean activo,
            @QueryParam("tipoDocumento") String tipoDocumento) {
        return String.valueOf(
                usuarioEJB.countFiltro(sexo, activo, tipoDocumento)
        );
    }

    @GET
    @Path("excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportExcel(
            @QueryParam("desde") Integer desde,
            @QueryParam("hasta") Integer hasta,
            @QueryParam("sexo") String sexo,
            @QueryParam("activo") Boolean activo,
            @QueryParam("tipoDocumento") String tipoDocumento) {
        
        System.out.println("AQUI COMO VA");

        try {
            List<Usuario> listado = usuarioEJB.findUsuariosFiltro((desde != null && hasta != null ? new int[]{desde, hasta} : null),
                    sexo, activo, tipoDocumento);

            return exportExcel(listado);
        } catch (Exception e) {
            return null;
        }
    }

    private Response exportExcel(List<Usuario> lista) {

        File file = new File("Reporte_usuarios.xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            //Libro Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            //Hoja de reporte
            XSSFSheet spreadsheet = workbook.createSheet("Reporte Usuarios");
            //Encabezados
            final String[] titles = {
                "No.", "TIPO DOCUMENTO", "No. DOCUMENTO", "NOMBRES", "APELLIDOS", "EMAIL",
                "SEXO", "ACTIVO"
            };
            int rowCount = 0;
            //header row
            Row headerRow = spreadsheet.createRow(rowCount++);
            Cell headerCell;

            for (int i = 0; i < titles.length; i++) {
                headerCell = headerRow.createCell(i);
                headerCell.setCellValue(titles[i]);
            }

            //Contenido reporte
            int secuencia = 1;
            for (Usuario resumen : lista) {
                Row row = spreadsheet.createRow(rowCount++);
                int cellCount = 0;

                //Id
                Cell cell = row.createCell(cellCount++);
                cell.setCellValue(secuencia);

                //TIPO DOCUMENTO
                cell = row.createCell(cellCount++);
                cell.setCellValue(resumen.getTipoDocumento().getId());

                //No. DOCUMENTO
                cell = row.createCell(cellCount++);
                cell.setCellValue(resumen.getNumDocumento());

                //NOMBRES  
                cell = row.createCell(cellCount++);
                cell.setCellValue(resumen.getNombres());

                //APELLIDOS  
                cell = row.createCell(cellCount++);
                cell.setCellValue(resumen.getApellidos());

                //EMAIL   
                cell = row.createCell(cellCount++);
                if (resumen.getEmail() != null) {
                    cell.setCellValue(resumen.getEmail());
                } else {
                    cell.setCellValue("");
                }

                //SEXO   
                cell = row.createCell(cellCount++);
                cell.setCellValue(resumen.getSexo());

                //ACTIVO
                cell = row.createCell(cellCount++);
                if (resumen.getActivo()) {
                    cell.setCellValue("SI");
                } else {
                    cell.setCellValue("NO");
                }
                secuencia++;
            }

            //Auto size columns
            for (int i = 0; i < titles.length; i++) {
                spreadsheet.autoSizeColumn(i);
            }

            //Escribe el archivo
            workbook.write(outputStream);
            outputStream.close();

            Response.ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            return response.build();
        } catch (IOException ex) {
            return null;
        }

    }

}
