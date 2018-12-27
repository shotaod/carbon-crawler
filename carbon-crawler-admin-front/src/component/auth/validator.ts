import * as Yup from "yup";

const password = () => Yup.string()
  .min(8, 'password length must be greater than 8')
  .max(32, 'password length must be less than 32')
  .required('required: password')

export const validator = {
  password,
}
