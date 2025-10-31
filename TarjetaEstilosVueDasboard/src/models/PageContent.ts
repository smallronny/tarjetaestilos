interface ItemContents {
  id: number,
  description: string,
  icon: string
}


class PageContent{
    title:string|null;
    description:string|null;
    image:string|null;
    image_position:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    menu_id:number|null;
    itemContents: ItemContents[]

    constructor(
        title:string|null,
        description:string|null,
        image:string|null,
        image_position:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, 
        id:number|null=null,
        menu_id:number|null=null,
        itemContents: ItemContents[]=[]
    ){
        this.title = title;
        this.description = description;
        this.image = image;
        this.image_position = image_position;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.menu_id = menu_id;
        this.itemContents = itemContents;
    }
};
export default PageContent;