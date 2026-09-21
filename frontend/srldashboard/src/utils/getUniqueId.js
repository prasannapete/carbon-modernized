import { nanoid } from "nanoid";

const getUniqueId = (idSize = 21) => {
  return nanoid(idSize);
};

export default getUniqueId;
