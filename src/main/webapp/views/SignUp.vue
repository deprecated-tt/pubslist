<template>
	<div class='signup'>
		<v-container class='fill-height' fluid>
			<v-row align='center' justify='center'>
				<v-col cols='12' sm='8' md='4'>
					<v-card class='elevation-12'>
						<v-toolbar color='secondary' dark flat>
							<img class='logo' alt='Pubs logo' src='../assets/logo.png'/>
							<v-toolbar-title class='headline mb-1'>Sign up</v-toolbar-title>
						</v-toolbar>
						<v-card-text>
							<v-form v-model='valid' ref='form'>
								<div><img :src='this.image' alt='' class='user-image'/></div>
								<v-text-field
										label='Username'
										name='username'
										type='text'
										:rules='usernameRules'
										counter
										hint='More than 6 characters, letters and numbers'
										required
										v-model='username'/>

								<v-text-field
										label='First name'
										name='firstname'
										type='text'
										required
										:rules='[v => !!v || "First name is required"]'
										v-model='firstName'/>

								<v-text-field
										label='Last name'
										name='lastname'
										type='text'
										required
										:rules='[v => !!v || "Last name is required"]'
										v-model='lastName'/>

								<v-text-field
										label='Password'
										name='password'
										type='password'
										:rules='passwordRules'
										counter
										hint='Provide password'
										required
										v-model='password'/>

								<v-text-field
										id='password'
										label='Confirm password'
										name='confirm_password'
										type='password'
										:rules='[v => v === this.password || "Passwords are not equal"]'
										hint='Confirm password'
										required/>

								<image-uploader @onImageUpload='onImageUpload'/>
							</v-form>
						</v-card-text>
						<v-card-actions>
							<v-btn text color='green darken-2' @click='goToLogin'>Have account? Login</v-btn>
							<v-spacer/>
							<v-tooltip bottom>
								<template v-slot:activator='{ on }'>
									<v-btn
											text
											icon
											color='red lighten-2'
											v-on='on'
											@click='clearForm'>
										<v-icon>mdi-delete</v-icon>
									</v-btn>
								</template>
								<span>Clear the form</span>
							</v-tooltip>
							<v-btn @click='signUp' :class='{ grey: !valid, green: valid }'>Sign Up</v-btn>
						</v-card-actions>
					</v-card>
				</v-col>
			</v-row>
		</v-container>
	</div>
</template>

<script>
	import ImageUploader from '../components/ImageUploader';
	import authApi from '../auth/authApi';

	export default {
		name: 'signup',
		components: {ImageUploader},
		data: function() {
			return {
				image: null,
				password: '',
				username: '',
				firstName: '',
				lastName: '',
				valid: false,
				passwordRules: [
					v => !!v || 'Password is required',
					v => (v && v.length >= 6) || 'Name must be more than 6 characters',
					v =>
						/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])/.test(v) ||
						'At least one uppercase, one lowercase, one digit required',
					v =>
						(v && v.length < 14) || 'Username should be less than 14 characters',
					v => v === this.password || 'Password are not equal'
				],
				usernameRules: [
					v => !!v || 'Username is required',
					v => (v && v.length >= 6) || 'Username must be more than 6 characters',
					v => /^[a-z0-9]+$/.test(v) || 'Only lowercase letters and numbers allowed',
					v =>
						(v && v.length < 14) || 'Username should be less than 14 characters'
				],
				imageRules: [
					value => !value || value.size < 2000000 || 'Avatar size should be less than 2 MB!',
				],
			};
		},
		methods: {
			signUp: function() {
				this.validateForm();

				if (this.valid) {
					authApi
						.post('api/signup', {
							username: this.username,
							password: this.password,
							name: this.firstName,
							surname: this.lastName,
							image: this.image
						})
						.then(
							() => {
								this.$router.replace('login');
							},
							(error) => {
								if(error.response.status === 400
									&& error.response.data.message === 'Username is already in use'){
									this.$toastr.e('Username is already in use');
								} else {
									this.$toastr.e('Ups... Something went wrong');
								}
							}
						);
				}
			},
			goToLogin() {
				this.$router.replace('login');
			},
			validateForm() {
				this.$refs.form.validate();
			},
			clearForm() {
				this.$refs.form.reset();
			},
			onImageUpload(dataUrl) {
				this.image = dataUrl;
			}
		}
	};
</script>

<style scoped lang="scss">
	.signup {
		display: block;
		height: 100%;
		width: 100%;

		.logo {
			height: 64px;
			width: 64px;

		}
	}

	.user-image {
		max-height: 100%;
		max-width: 100%;
	}
</style>
