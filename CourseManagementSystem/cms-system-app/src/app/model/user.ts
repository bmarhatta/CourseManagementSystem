export class User{
    public userId:string;
    public name:string;
    public username:string;
    public email:string;
    public profileImageUrl:string;
    public lastLoginDateDisplay:Date;
    public joinDate:Date;
    public roles:string;
    public authorities:[];
    public active:boolean;
    public notLocked:boolean;
}