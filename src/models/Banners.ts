interface Buttons {
  id: number,
  title: string,
  add_class: string,
  link: string
}

class Banners{
    title:string|null;
    subtitle:string|null;
    image:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    menu_id:number|null;
    buttons: Buttons[];
    label:string|null;
    blog_form:boolean|null;

    constructor(
        title:string|null,
        subtitle:string|null,
        image:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, id:number|null=null,
        menu_id:number|null=null,
        buttons: Buttons[]=[],
        label: string|null=null,
        blog_form: boolean|null = null,
    ){
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.menu_id = menu_id;
        this.buttons = buttons;
        this.label = label;
        this.blog_form = blog_form;
    }
};
export default Banners;