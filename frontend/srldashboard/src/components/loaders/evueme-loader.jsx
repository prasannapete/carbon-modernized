
import { FadeLoader } from "react-spinners";
import '../../css/Loader.css'

// `contained` renders the loader as an overlay scoped to its positioned parent (e.g. only the
// dashboard content area) instead of the default full-viewport overlay, so surrounding UI such as
// the filter section stays visible and usable while data loads.
const EvuemeLoader = ({ contained = false }) => {
  return (
    <div className={contained ? "overlay overlay--contained" : "overlay"}>
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
