class FrequentlyAskedQuestion{
    question:String|null;
    answer:String|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    menu_id:number|null;
    id:number|null;

    constructor(
        question:String|null,
        answer:String|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null,
        menu_id:number|null=null,
        id:number|null=null,
    ){
        this.question = question;
        this.answer = answer;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.menu_id = menu_id;
        this.id = id;
    }
};
export default FrequentlyAskedQuestion;