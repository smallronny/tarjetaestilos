class CorporateData{
    title:string|null;
    description:string|null;
    icon:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    menu_id:number|null;

    constructor(
        title:string|null,
        description:string|null,
        icon:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, 
        id:number|null=null,
        menu_id:number|null=null
    ){
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.menu_id = menu_id
    }
};
export default CorporateData;