
import { FadeLoader } from "react-spinners";
import '../../css/Loader.css'

const EvuemeLoader = () => {
  return (
    <div className="overlay">
      <div className="overlay__inner">
        <div className="overlay__content">
          <FadeLoader
            color="#a78540"
            loading={true}
            size={60}
            aria-label="Loading Spinner"
            data-testid="loader"
            height={15}
            width={5}
            radius={1000}
            margin={6}
          />

          {/*<EvuemeImageTag*/}
          {/*  className={"evueme-v-logo"}*/}
          {/*  imgSrc={image.evuemeVLogo}*/}
          {/*  altText={"Loading..."}*/}
          {/*/>*/}
        </div>
      </div>
    </div>
  );
};

export default EvuemeLoader;
