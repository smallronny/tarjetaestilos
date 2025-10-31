class Facility{
    label:string|null;
    title:string|null;
    button_label:string|null;
    image:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    menu_id:number|null

    constructor(
        label:string|null,
        title:string|null,
        button_label:string|null,
        image:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, id:number|null=null,
        menu_id:number|null=null
    ){
        this.label = label;
        this.title = title;
        this.button_label = button_label;
        this.image = image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.menu_id = menu_id;
    }
};
export default Facility;