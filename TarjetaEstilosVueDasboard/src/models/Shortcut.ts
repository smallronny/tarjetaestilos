class Shortcut{
    icon:String|null;
    title:String|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    menu_id:number|null;

    constructor(
        icon:String|null,
        title:String|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null,
        id:number|null=null,
        menu_id:number|null=null
    ){
        this.icon = icon;
        this.title = title;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.menu_id = menu_id;
    }
};
export default Shortcut;