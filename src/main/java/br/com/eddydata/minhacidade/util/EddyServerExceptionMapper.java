package br.com.eddydata.minhacidade.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EddyServerExceptionMapper implements ExceptionMapper<EddyServerException> {
    
    @Override
    public Response toResponse(EddyServerException exception) {
        ErrorMessage error = new ErrorMessage(exception.getMessage(), exception.getCode());
        if (exception.getCode() == ErrorCode.CLIENT_ERROR.getCode()) {
            return Response.status(Status.BAD_REQUEST)              
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if (exception.getCode() == ErrorCode.SERVER_ERROR.getCode()) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)                
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)                
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
