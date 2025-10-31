interface NetworkSelection {
  id: number,
  link: string
}


class Affiliates{
    title:string|null;
    image:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    networksIds:number[]|null=null 
    networks: NetworkSelection[]|null=null
    

    constructor(
        title:string|null,
        image:string|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null,
        networksIds:number[]|null=null, 
        networks:NetworkSelection[]|null=null
    ){
        this.title = title;
        this.image = image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.networksIds = networksIds;
        this.networks = networks;
    }
    
};
export default Affiliates;