import * as Yup from "yup";
import {validation} from "../../../service/validation";

export const querySchema = Yup.object().shape({
  title: Yup.string().max(16, 'input 16 characters or less').required('required'),
  url: Yup.string().url('illegal format, plz input url').required('required'),
  memo: Yup.string(),
  listing: Yup.object().shape({
    pagePath: Yup.string().max(255, 'input 255 characters or less').required('required'),
    linkQuery: validation.xpathQuery().required('required'),
  }),
  details: Yup.array()
    .of(Yup.object().shape({
      queryName: validation.inputText().required('required'),
      query: validation.xpathQuery().required('required'),
      type: Yup.string().required(),
    }))
})
