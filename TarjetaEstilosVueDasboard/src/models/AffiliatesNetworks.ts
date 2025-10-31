class AffiliatesNetworks{
    affiliates_id:number|null;
    social_networks_id:number|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    link:string|null;

    constructor(
        affiliates_id:number|null,
        social_networks_id:number|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null, link:string|null=null
    ){
        this.affiliates_id = affiliates_id;
        this.social_networks_id = social_networks_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.link = link;
    }
};
export default AffiliatesNetworks;