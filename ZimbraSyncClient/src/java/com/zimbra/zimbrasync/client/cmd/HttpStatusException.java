/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

@SuppressWarnings("serial")
public class HttpStatusException extends Exception {

    public static class BadRequestException extends HttpStatusException {}
    public static class UnauthorizedException extends HttpStatusException {}
    public static class ForbiddenException extends HttpStatusException {}
    public static class NotFoundException extends HttpStatusException {}
    public static class NeedProvisioningException extends HttpStatusException {}
    public static class WrongServerException extends HttpStatusException {}
    public static class ServerErrorException extends HttpStatusException {}
    
    public static HttpStatusException newException(int code) {
        switch (code) {
        case 400: //Bad Request
            return new BadRequestException();
        case 401: //Auth
            return new UnauthorizedException();
        case 403: //Sync not enabled for account
            return new ForbiddenException();
        case 404: //Not an AS server
            return new NotFoundException();
        case 449: //Need Provisioning
            return new NeedProvisioningException();
        case 451: //Wrong server
            return new WrongServerException();
        case 500: //Server error
            return new ServerErrorException();
        default:
            throw new RuntimeException("Unknown HTTP Status: " + code);
        }        
    }
}
