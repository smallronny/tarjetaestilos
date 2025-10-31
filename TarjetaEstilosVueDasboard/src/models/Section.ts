class Section{
    title:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    section_type_id:number|null;

    constructor(
        title:string|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null,section_type_id:number|null
    ){
        this.title = title;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.section_type_id =section_type_id
    }
};
export default Section;