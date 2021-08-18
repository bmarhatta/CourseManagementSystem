export interface CustomHttpResponse {   //this is going to represe4nt out custom http responce
    httpStatusCode: number;
    httpStatus:string;
    reason:string;
    message:string;
}
//where making this a interface because we do not want to inizilize it or set a value to the variables. 
//this is just for mapping. the backend will handle the acutally httoREsponces
