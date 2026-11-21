import pyray as pr

CHUNKSIZE = 50 # 50x50x50

def main():
    pr.init_window(500, 500, "Valid Geode Pos Helper")
    pr.rl_set_line_width(2)
    camera = pr.Camera3D()

    while not pr.window_should_close():
        pr.begin_drawing()

        pr.end_drawing()

if __name__ == "__main__":
    main()