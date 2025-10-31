

<template>
    <form  @submit.prevent="validateform(v$)">
        <div class="d-flex align-center text-center mb-6">
            <div class="text-h6 w-100 px-5 font-weight-regular auth-divider position-relative">
                <span class="bg-surface px-5 py-3 position-relative text-subtitle-1 text-grey100">Administrador Web</span>
            </div>
        </div>
        <div>
            <v-row class="mb-3">
                <v-col cols="12">
                    <v-label class="font-weight-medium mb-1">Correo</v-label>
                    <v-text-field 
                        variant="outlined" 
                        class="pwdInput"                         
                        color="primary"
                        v-model.trim="user.email" 
                        @input="v$.user.email.$touch" 
                        name="email" 
                        id="inputCorreo" 
                        :error="v$.user.email.$error || !this.credenciales"
                        :class="{
                            valid_form: v$.user.email.$dirty && !v$.user.email.$invalid
                        }"
                        :error-messages="v$.user.email.$errors.map(e => e.$message)"
                    ></v-text-field>
  
                </v-col>
                <v-col cols="12">
                    <v-label class="font-weight-medium mb-1">Contraseña</v-label>
                    
                    <v-text-field
                        v-model.trim="user.password"
                        @input="v$.user.password.$touch"
                        :type="showPassword ? 'text' : 'password'"
                        variant="outlined"
                        class="border-borderColor"
                        color="primary"
                        :error="v$.user.password.$error || !this.credenciales"
                        :class="{
                            valid_form: v$.user.password.$dirty && !v$.user.password.$invalid
                        }"
                        :error-messages="v$.user.password.$errors.map(e => e.$message)"
                    ></v-text-field>
                </v-col>
                    
                <v-col cols="12 " class="py-0">
                    <div class="d-flex flex-wrap align-center w-100 ">
                        <v-checkbox hide-details color="primary">
                            <template v-slot:label class="">Recordar</template>
                        </v-checkbox>
                        <div class="ml-sm-auto">
                            <RouterLink to=""
                                class="text-primary text-decoration-none text-body-1 opacity-1 font-weight-medium">
                                Recuperar Contraseña?</RouterLink>
                        </div>
                    </div>
                </v-col>
                <v-col cols="12">
                    <v-btn size="large" rounded="pill" color="primary" class="rounded-pill" block type="submit" flat>
                        Iniciar Sesión
                    </v-btn>
                </v-col>
            </v-row>
        </div>
    </form>
</template>
<script>
    import { VueFinalModal } from 'vue-final-modal';    
    import User from "../../models/User";
    import Global from "../../Global";
    import axios from "axios";
    //import swal from 'sweetalert';
    import useValidate from '@vuelidate/core';
    import { required, minLength, email, helpers  } from '@vuelidate/validators'
    import { useToast } from "vue-toastification";

    const toast = useToast()

    export default {
        name: 'Login-Component',
        components:{
            VueFinalModal            
        },
        data() {
            return {
                v$: useValidate(),
                file: "",
                url: Global.url,
                user: new User(null,null,null,null,null,null,null,null),
                submitted: false,
                showModalUserCreate: false,
                titleModalUserCreate:'Nuevo Usuario',
                submitOk: false,
                showPassword: false,
                credenciales: true
            };
        },
        validations: {
            user: {
                password: {
                    required: helpers.withMessage('La contraseña es requerido', required),
                    minLength: helpers.withMessage('La contraseña debe tener mínimo 6 caracteres', minLength(6)),
                    passwordComplex: helpers.withMessage(
                    'La contraseña debe contener al menos una letra, un número y un carácter especial',
                    helpers.regex(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]+$/)
                    )
                },
                email: {
                    required: helpers.withMessage('El correo es requerido', required),
                    minLength: helpers.withMessage('El correo debe tener mínimo 5 caracteres', minLength(5)),                    
                    email: helpers.withMessage('Debe tener formato de correo', email),
                }
            }
        },
        mounted() {
            if(localStorage.getItem('token')){
                this.$router.push("/dashboard");
                console.log(localStorage.getItem('token'));
            } 
        },
        methods: {
            nuevoUser(){
                this.showModalUserCreate = true;
            },
            ingresar(){
                //this.$route.push("/dashboard")
                window.location.href = '/dashboard'; 
            },
            flassmessagesuccess(statusText,name,time){                
                toast.success(`Bienvenido ${name}. ${statusText}`, {
                    timeout: time
                });
            },
            flassmessageerror(message,time){                
                toast.error(`${message}`, {
                    timeout: time
                });
            },
            validateform: function (v$) {
                console.log('v$:',v$);
                console.log('v$.user.email.$touch:',v$.user.email.$touch);
                console.log('v$.user.email.$error:',v$.user.email.$error);
                console.log('v$.user.email.$dirty:',v$.user.email.$dirty);
                console.log('v$.user.email.$invalid:',v$.user.email.$invalid);

                v$.$touch();
                if (v$.$invalid) {
                    // No continúa si hay errores
                    return;
                }

                // Aquí haces el axios solo si pasa la validación
                try {
                    this.submitOk = true;
                    axios
                        .post(this.url + "auth/login", this.user)
                        .then((response) => {
                            console.log(response);
                            if(response.data.status===200 && response.data.user){
                                //console.log('ingreso');
                                localStorage.setItem('token',response.data.token)
                                localStorage.setItem('nombre',response.data.user.fullname)
                                localStorage.setItem('imagen',response.data.user.image)
                                localStorage.setItem('rol',response.data.user.rol)
                                localStorage.setItem('email',response.data.user.email)
                                this.flassmessagesuccess(response.data.message,response.data.user.fullname);
                                this.$router.push("/dashboard");
                            }
                        
                        })
                        .catch((error) => {
                            console.log('error:',error);
                            if(error.response.data.status!==200){
                                this.credenciales = false;
                                this.flassmessageerror(error.response.data.message);
                                this.$nextTick(() => {
                                    const input = document.getElementById('inputCorreo');
                                    if (input) input.focus();
                                });
                            }
                            
                        });
                } catch (error) {
                    // ...manejo de error
                    console.log('error:',error);
                }
                    
                
            },           
        },
    }
</script>
