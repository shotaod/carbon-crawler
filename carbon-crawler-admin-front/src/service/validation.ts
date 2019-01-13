import * as Yup from "yup";

const password = () => Yup.string()
  .min(8, 'password length must be greater than 8')
  .max(32, 'password length must be less than 32')
  .required('required: password')

const inputText = () => Yup.string()
  .max(255, 'input 255 characters or less')

const xpathQuery = () => Yup.string()
  .matches(/xpath:\/\/.+/, 'currently supported xpath schema only')
  .max(255, 'input 255 characters or less')

export const validation = {
  password,
  inputText,
  xpathQuery,
}
